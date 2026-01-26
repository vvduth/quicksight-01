## Q : get middle node of linked list
*   do not know the length of linked list
*   can only loop through the linked list once
*   define 2 pointers: slow and fast
*   move slow pointer 1 step each time
*   move fast pointer 2 steps each time
*   when fast pointer reaches the end, slow pointer is at the middle
*   
## Q: has a loop?
*   define 2 pointers: slow and fast
*   move slow pointer 1 step each time
*   move fast pointer 2 steps each time
*   if slow pointer meets fast pointer, there is a loop
*   if fast pointer reaches the end, there is no loop
*   
## Q: kth node from the end
*   define 2 pointers: first and second
*   move first pointer k steps ahead
*   move both pointers 1 step each time
*  when first pointer reaches the end, second pointer is at the kth node from the end
*  
## Q: remove duplicates:
* using nested loop: O(n^2) time, O(1) space
* one loop has var called current
* another loop has var called runner
* current is current node  that we are checking for duplicates
* runner checks all subsequent nodes for duplicates
## binary to decimal linked list
* no length given, cannot calculate the length
* define current node = head
* assume that the list have one node 
* if current.next exist => 2 node, then decimal = decimal * 2 + current.next.value
* move current to current.next
## Partition list
* pass the list a value z
* all the nodes less than z come to one ide, maintain relative order
* all the nodes greater than or equal to z come to the other side, maintain relative order
* how to do this?
* create 2 temp dummy nodes: D1 and D2, prev1 and prev2 point to D1 and D2
* loop through the list:
* if current node value < z, prev1.next = current node, move prev1 to prev1.next
* else prev2.next = current node, move prev2 to prev2.next
* after loop, prev2.next = null
* prev1.next = D2.next
* return D1.next
## reverse between
