package ddl;

public class ddl {
    public static void main(String[] args) {
        DoublyLinkedList myDLL = new DoublyLinkedList(10);
        System.out.println("Head: " + myDLL.getHead().value);
        System.out.println("Tail: " + myDLL.getTail().value);
    }
}
