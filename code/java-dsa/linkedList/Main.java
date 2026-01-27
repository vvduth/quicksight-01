package linkedList;
public class Main {
    public static void main(String[] args) {
        

        int nums[] = {5, 3,  8, 6, 2};
        int size = nums.length;
        System.out.println("Before sorting:");
        for (int num : nums) {
            System.out.print(num + " ");
        }   
        
        // bubble sort
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (nums[j] > nums[j + 1]) {
                    // swap
                    int temp = nums[j];
                    nums[j] = nums[j + 1];
                    nums[j + 1] = temp;
                }
            }
        }
        System.out.println("\nAfter bubble sorting:");
        for (int num : nums) {
            System.out.print(num + " ");
        }

        // selection sort
        int nums2[] = {5, 3,  8, 6, 2};

        // selection sort
        for (int i = 0; i < size - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < size; j++) {
                if (nums2[j] < nums2[minIndex]) {
                    minIndex = j;
                }
            }
            // swap
            int temp = nums2[i];
            nums2[i] = nums2[minIndex];
            nums2[minIndex] = temp;
        }
        System.out.println("\nAfter selection sorting:");
        for (int num : nums2) {
            System.out.print(num + " ");
        }

        System.out.println("\n-----------------------------------------------");

        // insertion sort => O(n^2)
        int nums3[] = {5, 3,  8, 6, 2};
        System.out.println("\nBefore insertion sorting:");
        for (int num : nums3) {
            System.out.print(num + " ");
        }
        for (int i = 1; i < size; i++) {
            int key = nums3[i];
            int j = i - 1;
            while (j >= 0 && nums3[j] > key) {
                nums3[j + 1] = nums3[j];
                j--;
            }
            nums3[j + 1] = key;
        }

        System.out.println("\nAfter insertion sorting:");
        for (int num : nums3) {
            System.out.print(num + " ");
        }

        System.out.println("\n-----------------------------------------------");
        int nums4[] = {5, 3, 6, 1, 4, 2};


        // quick sort
        System.out.println("\nBefore quick sorting:");
        for (int num : nums4) {
            System.out.print(num + " ");
        }
        quickSort(nums4, 0, nums4.length - 1);
        System.out.println("\nAfter quick sorting:");
        for (int num : nums4) {
            System.out.print(num + " ");
        }
    }
    public static void quickSort (int[] arr, int low, int high) {
        
        if (low < high) {
            int pi = partition(arr, low, high);

            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }
    public static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;

                // swap arr[i] and arr[j]
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // swap arr[i + 1] and arr[high] (or pivot)
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }
}