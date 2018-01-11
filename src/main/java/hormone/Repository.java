package hormone;

import hormone.exception.ConsistencyException;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Il y a un repository par Mapper.
 * Un repository contient une identity map, avec des weak references.
 * C'est en le cache du mapper.
 * Il joue un role dans le unit of work. Lors d'un Mapper.flush, on parcours le cache à la recherche
 * d'instances dirty à mettre à jour.
 */
public class Repository {

    private Class<? extends Model> type;
    private HashMap<String, WeakReference<Model>> identityMap;

    public Repository(Class<? extends Model> type) {
        this.type = type;
        this.identityMap = new HashMap<>();
    }

    public Model get(String id) {
        WeakReference<Model> box = identityMap.getOrDefault(id, null);
        if (box == null) {
            return null;
        }
        else {
            Model value = box.get();
            if (value == null) {
                identityMap.remove(id);
            }
            return value;
        }
    }

    public void put(Model model) {
        if (!model.getClass().isAssignableFrom(type)) {
            throw new ConsistencyException(String.format(
                "You're putting a model '%s' in the wrong repository '%s'",
                model.getClass().getName(), type.getName()));
        }
        Model actual = get(model.getId());
        if (actual == null) {
            identityMap.put(model.getId(), new WeakReference<>(model));
        }
        else if (actual != model) {
            throw new ConsistencyException(String.format("two instance of type %s with the same key", type.getName()));
        }
    }

    public void remove(String id) {
        identityMap.remove(id);
    }

    /**
     * Package-private method very useful for flexible search within the repository
     * @return a stream of unboxed model filtered so there is no null values.
     */
    Stream<Model> stream() {
        return identityMap.values().stream().filter(wr -> wr.get() != null).map(WeakReference::get);
    }
}
