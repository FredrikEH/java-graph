public class Edge<T> {

    private String name;
    private T destination;
    private int weight;

    public Edge(String name, T destination, int weight){
        this.name = name;
        this.destination = destination;
        this.weight = weight;
    }

    public String getName(){
        return name;
    }

    public T getDestination(){
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int newWeight){
        if(newWeight < 0){
            throw new IllegalArgumentException("Tal mindre än noll");
        }
        this.weight = newWeight;
    }

    @Override
    public String toString() {
        return "till " + destination + " med " + name + " tar " + weight;
    }
}
