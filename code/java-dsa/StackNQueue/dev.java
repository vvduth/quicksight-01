package StackNQueue;

public class dev {
    static public String reverseString(String str) {
        Stack2<Character> myStack = new Stack2<>();
        for (int i = 0; i < str.length(); i++) {
            myStack.push(str.charAt(i));
        }
        String reversedStr = "";
        while (!myStack.isEmpty()) {
            reversedStr += myStack.pop();
        }
        System.out.println("Reversed string: " + reversedStr);
        return reversedStr;
    }

    static public void sortStack(Stack2<Integer> inputStack) {
        Stack2<Integer> tempStack = new Stack2<>();
        while (!inputStack.isEmpty()) {
            int temp = inputStack.pop();
            while (!tempStack.isEmpty() && tempStack.peek() > temp) {
                inputStack.push(tempStack.pop());
            }
            tempStack.push(temp);
        }
        while (!tempStack.isEmpty()) {
            inputStack.push(tempStack.pop());
        }
    }
    static public boolean isBalancedParentheses(String str) {
        Stack2<Character> stack = new Stack2<>();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '(') {
                stack.push(ch);
            } else if (ch == ')') {
                if (stack.isEmpty()) {
                    return false;
                }
                stack.pop();
            }
        }
        return stack.isEmpty();
    }
    public static void main(String[] args) {
         Stack2<Integer> stack;

        System.out.println("These tests confirm sortStack sorts");
        System.out.println("the stack so the TOP is the LOWEST value.");
        System.out.println("printStack() shows the stack from top");
        System.out.println("to bottom (smallest to largest).");
        System.out.println();

        // Test 1: Empty stack
        System.out.println("Test 1: Empty Stack");
        stack = new Stack2<>();
        sortStack(stack);
        System.out.println("Expected (top to bottom): empty");
        stack.printStack();
        System.out.println();

        // Test 2: Single element
        System.out.println("Test 2: Single Element");
        stack = new Stack2<>();
        stack.push(5);
        sortStack(stack);
        System.out.println("Expected (top to bottom): 5");
        stack.printStack();
        System.out.println();

        // Test 3: Unsorted stack
        System.out.println("Test 3: Unsorted Stack");
        stack = new Stack2<>();
        stack.push(3);
        stack.push(1);
        stack.push(4);
        stack.push(2);
        sortStack(stack);
        System.out.println("Expected (top to bottom): 1, 2, 3, 4");
        stack.printStack();
        System.out.println();

        // Test 4: Already sorted
        System.out.println("Test 4: Already Sorted Stack");
        stack = new Stack2<>();
        stack.push(4);
        stack.push(3);
        stack.push(2);
        stack.push(1);
        sortStack(stack);
        System.out.println("Expected (top to bottom): 1, 2, 3, 4");
        stack.printStack();
        System.out.println();

        // Test 5: Reverse sorted
        System.out.println("Test 5: Reverse Sorted Stack");
        stack = new Stack2<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        sortStack(stack);
        System.out.println("Expected (top to bottom): 1, 2, 3, 4");
        stack.printStack();
        System.out.println();

        // Test 6: With duplicates
        System.out.println("Test 6: With Duplicates");
        stack = new Stack2<>();
        stack.push(3);
        stack.push(1);
        stack.push(3);
        stack.push(2);
        stack.push(1);
        sortStack(stack);
        System.out.println("Expected (top to bottom): 1, 1, 2, 3, 3");
        stack.printStack();
        System.out.println();

        // Test 7: With negatives
        System.out.println("Test 7: With Negatives");
        stack = new Stack2<>();
        stack.push(-1);
        stack.push(3);
        stack.push(-5);
        stack.push(2);
        sortStack(stack);
        System.out.println("Expected (top to bottom): -5, -1, 2, 3");
        stack.printStack();
        System.out.println();

        
    }
}
