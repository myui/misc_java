package leetcode;

import java.util.Arrays;

/**
 * 
 * Input: nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3
 * Output: [1,2,2,3,5,6]
 * Explanation: The arrays we are merging are [1,2,3] and [2,5,6].
 * The result of the merge is [1,2,2,3,5,6] with the underlined elements coming from nums1.
 * 
 * constraints:
 *  nums1.length == m + n
 *  nums2.length == n
 *  0 <= m, n <= 200
 *  1 <= m + n <= 200
 *  -109 <= nums1[i], nums2[j] <= 109
 * 
 * {@link https://leetcode.com/problems/merge-sorted-array/}
 */
public class MergeSortedArray {

    public void merge(int[] nums1, int m, int[] nums2, int n) {
        assert (nums1.length == (m+n));
        assert (nums2.length == n);
        
        for(int i=m-1, j=n-1, k = m+n-1; k>=0 && j>=0; k--) {
            if(i < 0 || nums2[j] > nums1[i]) {
                nums1[k] = nums2[j--];
            } else {
                nums1[k] = nums1[i--];
            }
        }
    }
    
    public static void main(String[] args) {
        int[] num1 = new int[] {1,2,3,0,0,0};
        int m = 3;
        int[] num2 = new int[] {2,5,6};
        int n = 3;
        MergeSortedArray solution = new MergeSortedArray();
        solution.merge(num1, m, num2, n);
        System.out.println(Arrays.toString(num1));
                
        num1 = new int[] {0};
        m = 0;
        num2 = new int[] {1};
        n = 1;
        solution = new MergeSortedArray();
        solution.merge(num1, m, num2, n);
        System.out.println(Arrays.toString(num1));
    }    
    
}