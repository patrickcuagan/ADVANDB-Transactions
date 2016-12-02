package View;

import java.util.Comparator;

class SecondWordComparator implements Comparator<String>{
    @Override
    public int compare(String s1, String s2)
    {
        String[] a1 = s1.split(" ");
        String[] a2 = s2.split(" ");

        // you should ensure that there are actually two elements here
        return a1[1].compareTo(a2[1]);
    }
}
