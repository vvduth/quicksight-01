## doubly linked list in java
* define a class Node with value, next, and prev
* define a class DoublyLinkedList with head, tail, and length
* constructor initializes head and tail to a new node with given value, length to 1
* next will point to the next node, prev will point to the previous node
## preend:
* add noew node at the beginning
* if list is empty, set head and tail to new node

## get: 
* retrieve node at given index
### exercise with dll:

1. palimdrome check: i.e racecar is palindrome, you given the length, we will have 2 vars, forward and backward, if forward.val != backward.val return false, else move forward to next and backward to prev, until they meet in the middle
2. ddl reserver: we need 1 var named current and temp, switc the pointers of current, temp = current, then swap head and tail.
3. partition list:
