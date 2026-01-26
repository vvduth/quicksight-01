package linkedList;
public class Main {
    public static void main(String[] args) {
        LinkedList myLinkedList = new LinkedList(10);
        myLinkedList.append(5);
        myLinkedList.append(16);
        myLinkedList.prepend(1);
        myLinkedList.printList();
        
        System.out.println("Index 2: " + myLinkedList.get(2).value);
        myLinkedList.set(2, 9);
        
        System.out.println("Index 2: " + myLinkedList.get(2).value);
        
       
    }
}