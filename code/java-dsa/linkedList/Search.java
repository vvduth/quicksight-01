package linkedList;

public class Search {
    private int nums[] = {5, 7, 9,12, 15, 18, 20, 25, 30, 35};
    private int target = 15;
    public int binarySearch() {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            }
            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
            
        }

        return -1;
    }
    public void linearSearch() {
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) {
                System.out.println("Target found at index: " + i);
                return;
            }
        }
    } 
}
