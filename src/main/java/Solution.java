public class Solution {

    public static void main(String[] args) {
        String fileName = "./src/main/file.txt";
        int numberOfLines = 500;
        CreateNewFile.createNewFile(fileName);
        StringGenerator.generateLinesToFile(numberOfLines, 50, fileName);
        StringSorter.sortLinesInFile(fileName, 3, 20);
    }
}
