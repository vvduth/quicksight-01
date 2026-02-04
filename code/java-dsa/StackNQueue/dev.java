package StackNQueue;

public class dev {
    public static void main(String[] args) {
        Stack myStack = new Stack(10);
        myStack.push(12);
        myStack.push(15);
        myStack.pop();
        myStack.printStack();
        myStack.getTop();
        myStack.getHeight();
    }
}
