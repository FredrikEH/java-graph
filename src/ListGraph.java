import java.util.*;

public class ListGraph<T> implements Graph<T>{

    private Map<T, Set<Edge>> nodes = new HashMap<T, Set<Edge>>();

    public void add(T node){
        if(!nodes.containsKey(node)) {
            nodes.put(node, new HashSet<Edge>());
        }
    }

    public void remove(T node) throws NoSuchElementException{
        if(!nodes.containsKey(node)){
            throw new NoSuchElementException("No such node");
        }
        for(Edge<T> edge : nodes.get(node)){
            T destination = edge.getDestination();
            nodes.get(destination).removeIf(e -> e.getDestination() == node);
        }
        nodes.remove(node);
    }

    @Override
    public Set<T> getNodes(){
        Set<T> result = new HashSet<>();
        for(T node : nodes.keySet()){
            result.add(node);
        }
        return result;
    }

    public void connect(T node1, T node2, String name, int weight){
        if(!nodes.containsKey(node1)){
            throw new NoSuchElementException("No such node: " + node1);
        }
        if(!nodes.containsKey(node2)){
            throw new NoSuchElementException("No such node: " + node2);
        }
        if(weight < 0){
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        if(getEdgeBetween(node1, node2) != null){
            throw new IllegalStateException("Edge already exists");
        }
        Edge edgeFromNode1ToNode2 = new Edge(name, node2, weight);
        Edge edgeFromNode2ToNode1 = new Edge(name, node1, weight);
        Set<Edge> setNode1 = nodes.get(node1);
        Set<Edge> setNode2 = nodes.get(node2);
        setNode1.add(edgeFromNode1ToNode2);
        setNode2.add(edgeFromNode2ToNode1);
    }

    public void setConnectionWeight(T node1, T node2, int weight){
        if(getEdgeBetween(node1, node2) == null){
            throw new NoSuchElementException("No such node");
        }
        if(weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        Set<Edge> set1 = nodes.get(node1);
        for(Edge edge : set1){
            if(edge.getDestination() == node2){
                edge.setWeight(weight);
            }
        }
        Set<Edge> set2 = nodes.get(node2);
        for(Edge edge : set2){
            if(edge.getDestination() == node1){
                edge.setWeight(weight);
            }
        }
    }

    public Edge<T> getEdgeBetween(T node1, T node2){
        if(!nodes.containsKey(node1)){
            throw new NoSuchElementException("No such node: " + node1);
        }
        if(!nodes.containsKey(node2)){
            throw new NoSuchElementException("No such node: " + node2);
        }
        for(Edge edge : nodes.get(node1)){
            if(edge.getDestination() == node2){
                return edge;
            }
        }
        return null;
    }

    public void disconnect(T node1, T node2){
        if(!nodes.containsKey(node1)){
            throw new NoSuchElementException("No such node: " + node1);
        }
        if(!nodes.containsKey(node2)){
            throw new NoSuchElementException("No such node: " + node2);
        }
        if(getEdgeBetween(node1, node2) == null){
            throw new IllegalStateException("No such edge");
        }
        nodes.get(node1).remove(getEdgeBetween(node1, node2));
        nodes.get(node2).remove(getEdgeBetween(node2, node1));
    }

    public Collection<Edge<T>> getEdgesFrom(T node){
        if(!nodes.containsKey(node)){
            throw new NoSuchElementException("No such node");
        }
        Collection<Edge<T>> result = new ArrayList<>();
        for(Edge edge : nodes.get(node)){
            result.add(edge);
        }
        return result;
    }

    private void depthFirstSearch(T where, Set<T> visited){
        visited.add(where);
        for(Edge edge : nodes.get(where)){
            if(!visited.contains(edge.getDestination())){
                depthFirstSearch((T)edge.getDestination(), visited);
            }
        }
    }

    private void depthFirstSearch(T where, T fromWhere, Set<T> visited, Map<T, T> via){
        visited.add(where);
        via.put(where, fromWhere);
        for(Edge edge : nodes.get(where)){
            if(!visited.contains(edge.getDestination())){
                depthFirstSearch((T)edge.getDestination(), where, visited, via);
            }
        }
    }

    @Override
    public boolean pathExists(T from, T to) {
        if(!nodes.containsKey(from) || !nodes.containsKey(to)){
            return false;
        }
        Set<T> visited = new HashSet<>();
        depthFirstSearch(from, visited);
        return visited.contains(to);
    }

    private List<Edge<T>> gatherPath(T from, T to, Map<T, T> via){
        List<Edge<T>> result = new ArrayList<>();
        T where = to;
        while(!where.equals(from)){
            T node = via.get(where);
            Edge edge = getEdgeBetween(node, where);
            result.add(edge);
            where = node;
        }
        Collections.reverse(result);
        return result;
    }

    @Override
    public List<Edge<T>> getPath(T from, T to) {
        if(!pathExists(from, to)){
            return null;
        }
        Map<T, T> via = new HashMap<>();
        Set<T> visited = new HashSet<>();
        depthFirstSearch(from, null, visited, via);
        List<Edge<T>> result = gatherPath(from, to, via);
        return result;
    }

    public String toString(){
        String result = "";
        for(T node : nodes.keySet()){
            result += node + ";";
            for(Edge edge : nodes.get(node)){
                result += edge + ";";
            }
        result += "\n";
        }
        return result;
    }
}
