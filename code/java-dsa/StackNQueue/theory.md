### example of when to use stack:
* undo/redo operations in text editors
* expression evaluation (e.g., converting infix to postfix notation)
* parsing (e.g., syntax checking in compilers)
* O(1) for both adding or removing items at the end
* O(n) for adding or removing items at the beginning or in the middle
* implemented using linked list cuz it is dynamic in size and O(1) for adding/removing at head
* in this case head is named top, and we only add/remove from the top, tail is bottom


### Queue
* FIFO data structure (first in first out)
* used in breadth-first search (BFS) algorithms
* enqueue on one end and dequeue on the other end
* O(1) for both adding or removing items at the ends
* O(n) for adding or removing items in the middle
* implemented using linked list cuz it is dynamic in size and O(1) for adding/removing at head/tail
* in queue, head is named front, tail is named back