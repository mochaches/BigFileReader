public class Solution {

    public static void main(String[] args) {
        String fileName = "./src/main/file.txt";
        int numberOfLines = 100;
        CreateNewFile.createNewFile(fileName);
        StringGenerator.generateLinesToFile(numberOfLines, 5, fileName);
        StringSorter.sortLinesInFile(fileName, 3, 10);
    }
}
