public class Solution {

    public static void main(String[] args) {
        String fileName = "./src/main/file.txt";
        int numberOfLines = 50000000;
        CreateNewFile.createNewFile(fileName);
        StringGenerator.generateLinesToFile(numberOfLines, 500, fileName);
        StringSorter.sortLinesInFile(fileName, 3, 5000);
    }
}
