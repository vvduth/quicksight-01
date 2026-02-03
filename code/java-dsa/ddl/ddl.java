package ddl;

public class ddl {
    public static void main(String[] args) {
        DoublyLinkedList myDLL = new DoublyLinkedList(1);
        myDLL.append(3);
        myDLL.insert(2, 1);
        myDLL.printList();
    }
}
