package StackNQueue;

public class Queue {
    private Node first;
    private Node last;
    private int length;

    public Queue(int value) {
        Node newNode = new Node(value);
        first = newNode;
        last = newNode;
        length = 1;
    }
    public void printQueue() {
        Node temp = first;
        while (temp != null) {
            System.out.println(temp.value);
            temp = temp.next;
        }
    }
    public void getFirst() {
        if (length == 0) {
            System.out.println("Queue is empty");
        } else {
            System.out.println("First value: " + first.value);
        }
    }

    public void getLast() {
        if (length == 0) {
            System.out.println("Queue is empty");
        } else {
            System.out.println("Last value: " + last.value);
        }
    }

    public void getLength() {
        System.out.println("Queue length: " + length);
    }

    // similar to to append in linked list
    public void enqueue(int value) {
        Node newNode = new Node(value);
        if (length == 0) {
            first = newNode;
            last = newNode;
        } else {
            last.next = newNode;
            last = newNode;
        }
        length++;
    }

    public Node dequeue() {
        if (length == 0) {
            return null;
        }
        Node temp = first;
        if (length == 1) {
            first = null;
            last = null;
        } else {
            first = first.next;
            temp.next = null;
        }
        length--;
        return temp;
    }
}
