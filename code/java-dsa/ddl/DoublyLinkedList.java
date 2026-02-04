package ddl;

public class DoublyLinkedList {
    private Node head;
    private Node tail;
    private int length;

    class Node {
        int value;
        Node next;
        Node prev;

        Node(int value) {
            this.value = value;
        }

        Node() {
        }
    }

    public DoublyLinkedList(int value) {
        Node newNode = new Node(value);
        head = newNode;
        tail = newNode;
        length = 1;
    }

    public DoublyLinkedList() {
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }

    public int getLength() {
        return length;
    }

    public void printList() {
        Node temp = head;
        while (temp != null) {
            System.out.println(temp.value);
            temp = temp.next;
        }
    }

    public void append(int value) {
        Node newNode = new Node(value);
        if (length == 0) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        length++;
    }

    public Node removeLast() {
        if (length == 0)
            return null;
        Node temp = tail;
        if (length == 1) {
            head = null;
            tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
            temp.prev = null;
        }
        length--;
        return temp;
    }

    public void prepend(int value) {
        Node newNode = new Node(value);
        if (length == 0) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        length++;
    }

    public Node removeFirst() {
        if (length == 0)
            return null;
        Node temp = head;
        if (length == 1) {
            head = null;
            tail = null;
        } else {
            head = head.next;
            head.prev = null;
            temp.next = null;
        }
        length--;
        return temp;
    }

    public Node get(int index) {
        if (index < 0 || index > length) {
            return null;
        }
        Node temp = head;
        if (index < length / 2) {
            for (int i = 0; i < index; i++) {
                temp = temp.next;
            }
        } else {
            temp = tail;
            for (int i = length - 1; i > index; i--) {
                temp = temp.prev;
            }
        }

        return temp;
    }

    public boolean set(int index, int value) {
        Node temp = get(index);
        if (temp != null) {
            temp.value = value;
            return true;
        }
        return false;

    }

    public boolean insert(int value, int index) {
        if (index < 0 || index > length) {
            return false;
        }
        if (index == 0) {
            prepend(value);
            return true;
        }
        if (index == length) {
            append(value);
            return true;
        }
        Node newNode = new Node(value);
        Node before = get(index - 1);
        Node after = before.next;
        newNode.prev = before;
        newNode.next = after;
        before.next = newNode;
        after.prev = newNode;
        length++;
        return true;
    }

    public Node remove(int index) {
        if (index < 0 || index >= length) {
            return null;
        }
        if (index == 0) {
            removeFirst();
        }
        if (index == length - 1) {
            removeLast();
        }
        Node temp = get(index);
        Node before = temp.prev;
        Node after = temp.next;

        temp.prev = null;
        temp.next = null;
        before.next = after;
        after.prev = before;
        length--;
        return temp;
    }

    // another way of remove
    public Node remove2(int index) {
        if (index < 0 || index >= length) {
            return null;
        }
        if (index == 0) {
            removeFirst();
        }
        if (index == length - 1) {
            removeLast();
        }
        Node temp = get(index);
        temp.next.prev = temp.prev;
        temp.prev.next = temp.next;
        temp.prev = null;
        temp.next = null;
        length--;
        return temp;
    }

    public boolean isPalindrome() {
        if (length == 1) {
            return true;
        }
        Node forward = head;
        Node backward = tail;

        for (int i = 0; i < length / 2; i++) {
            if (forward.value != backward.value) {
                return false;
            }
            forward = forward.next;
            backward = backward.prev;
        }

        return true;
    }

    public void reverse() {
        Node current = head;
        Node temp = null;

        while (current != null) {
            temp = current.prev;
            current.prev = current.next;
            current.next = temp;
            current = current.prev;
        }

        temp = head;
        head = tail;
        tail = temp;
    }

    public void partitionList(int x) {
        // If the list is empty, nothing to do
        if (head == null)
            return;

        // Create two dummy nodes to help build two new lists
        Node dummy1 = new Node(0); // List for nodes < x
        Node dummy2 = new Node(0); // List for nodes >= x

        // Use these pointers to build the two lists
        Node prev1 = dummy1;
        Node prev2 = dummy2;
        Node current = head;

        // Traverse the original list
        while (current != null) {
            if (current.value < x) {
                // Attach node to dummy1 list
                prev1.next = current;
                current.prev = prev1;
                prev1 = current;
            } else {
                // Attach node to dummy2 list
                prev2.next = current;
                current.prev = prev2;
                prev2 = current;
            }
            current = current.next;
        }

        // End the second list to avoid any trailing connections
        prev2.next = null;

        // Connect the two lists
        prev1.next = dummy2.next;

        // If dummy2 list has nodes, fix their prev pointer
        if (dummy2.next != null) {
            dummy2.next.prev = prev1;
        }

        // Update head pointer of the main list
        head = dummy1.next;

        // Ensure new head has no previous pointer
        if (head != null) {
            head.prev = null;
        }
    }

    public void swapPairs() {
        if (head == null || head.next == null) {
            return;
        }
        Node dummyNode = new Node(0);
        dummyNode.next = head;
        Node prev = dummyNode;
        

        while (head != null && head.next != null) {
            Node first = head;
            Node second = first.next;

            // swap
            prev.next = second;
            first.next = second.next;
            second.next = first;

            // swap prev
            second.prev = prev;
            first.prev = second;
            if (first.next != null) {
                first.next.prev = first;
            }
            
            // move pointers to prepare for the next iteration
            head = first.next;
            prev = first; 

        }
        head = dummyNode.next;
        if (head != null) head.prev = null;

    }

}
