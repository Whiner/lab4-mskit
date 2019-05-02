package org.donntu.knt.mskit.lab4;

/**
 * @author Shilenko Alexander
 */
public class Main {
    public static void main(String[] args) {
        String string = "Всем братьям Салам Всем братьям Салам Всем братьям Салам Всем братьям Салам";

        MD4 md4 = new MD4();

        System.out.println(md4.hashCode(string));
        System.out.println(md4.hashCode(string));
        System.out.println(md4.hashCode(string));
    }
}
