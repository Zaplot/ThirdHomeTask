import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

class Methods {
    private boolean checkExistence(Path path) {
        return new File(path.toString()).exists();
    }

    private void deleteFile(Path path) {
        if (path.toFile().delete())
            System.out.println("File has been deleted!");
        else
            System.out.println("File hasn't been deleted!");

    }

    void getInfo() {
        String separator = "";
        for (int i = 0; i < 175; i++) {
            separator += "*";
            if (i == 174)
                separator += "\n";
        }
        System.out.println(separator + "*1. RenameFile() method gets path to file which you want to rename and name for renaming\n" +
                separator + "*2. ReplaceFile() method gets path to file which you want to remove and removes it. In case working " +
                "with directories the method wouldn't remove files included in directory!\n" + separator +
                "*3. FilesEncounter(String path) method takes the root path and displays a list of all the files in the folders and subfolders and counts them\n" + separator +
                "*4. ZipDirectory(File zippingFile, String path, ZipOutputStream zipOut) method takes path to directory and path where you want to create zipped directory\n" + separator +
                "*5. UnzipDirectory(String path, String pathTo) method takes path to zipped directory and path where you want to unzip files\n" + separator
        );
    }

    void renameFile() {
        while (true) {
            Scanner in = new Scanner(System.in);
            System.out.println("Write the path to your file");
            String direction = in.next();
            Path path = Paths.get(direction);
            File file = new File(path.toFile().getParent(), path.getFileName().toString());
            if (!checkExistence(path)) {
                System.out.println("There is no such file!");
                continue;
            }
            System.out.println("How do you want to rename your file?");
            String name = in.next();
            File file2 = new File(path.toFile().getParent(), name);

            if (file.renameTo(file2)) {
                System.out.println("Successfully!");
                break;
            } else {
                System.out.println("Error! No such file or file with this name already exist!");
                break;
            }
        }
    }

    void replaceFile() {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("Write the path to your file");
            Path path = Paths.get(in.next());
            if (!checkExistence(path)) {
                System.out.println("There is no such file!");
                continue;
            }
            System.out.println("Write the path where you want to remove your file");
            Path pathTo = Paths.get(in.next(), path.getFileName().toString());
            try {
                System.out.println("Do you want to delete file from the root folder during removing?\n Yes/No");
                String delete = in.next();
                if (checkExistence(pathTo)) {
                    System.out.println("Do you want to replace existing file?\nYes/No");
                    if (in.next().toLowerCase().equals("Yes"))
                        Files.copy(path, pathTo, StandardCopyOption.REPLACE_EXISTING);
                } else
                    Files.copy(path, pathTo, StandardCopyOption.COPY_ATTRIBUTES);
                if (delete.toLowerCase().equals("yes"))
                    deleteFile(path);
                System.out.println("File removed successfully");

                break;
            } catch (Exception e) {
                System.out.println("Seems like some troubles occurred");
            }
        }
    }

    List<File> filesEncounter(String path) {
        File directory = new File(path);
        List<File> resultList = new ArrayList<File>();
        if (checkExistence(directory.toPath())) {
            File[] fList = directory.listFiles();
            resultList.addAll(Arrays.asList(fList));
            for (File file : fList) {
                if (file.isFile()) {
                    System.out.println(file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    resultList.addAll(filesEncounter(file.getAbsolutePath()));
                }
            }
        } else {
            System.out.println("No such directory!");
            resultList = null;
        }
        return resultList;

    }

    void zipDirectory(File zippingFile, String path, ZipOutputStream zipOut) {
        try {
            if (zippingFile.isHidden()) {
                return;
            }
            if (zippingFile.isDirectory()) {
                if (path.endsWith("/")) {
                    zipOut.putNextEntry(new ZipEntry(path));
                    zipOut.closeEntry();
                } else {
                    zipOut.putNextEntry(new ZipEntry(path + "/"));
                    zipOut.closeEntry();
                }
                File[] innerFiles = zippingFile.listFiles();
                for (File childFile : innerFiles) {
                    zipDirectory(childFile, path + "/" + childFile.getName(), zipOut);
                }
                return;
            }
            FileInputStream fis = new FileInputStream(zippingFile);
            ZipEntry zipEntry = new ZipEntry(path);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
    }

    void unzipDirectory(String path, String pathTo) {
        byte[] buffer = new byte[1024];
        ZipInputStream zis;
        try {
            zis = new ZipInputStream(new FileInputStream(path));

            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(pathTo + fileName);
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            System.out.println("Something went wrong!");
            ;
        }
    }
}

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        String flag = "";
        Methods f = new Methods();
        while (!flag.equals("0")) {
            switch (flag) {
                case "1": {
                    f.getInfo();
                    break;
                }
                case "2": {
                    f.renameFile();
                    break;
                }
                case "3": {
                    f.replaceFile();
                    break;
                }
                case "4": {
                    System.out.println("Input path to your directory");
                    f.filesEncounter(in.next());
                    break;
                }
                case "5": {

                    System.out.println("Input path to your directory");
                    String filePath = in.next();
                    System.out.println("Input path to your zipped directory");
                    String zipPath = in.next();
                    File toZip = new File(filePath);
                    f.zipDirectory(toZip, toZip.getName(), new ZipOutputStream(new FileOutputStream(zipPath)));
                    break;
                }
                case "6": {

                    System.out.println("Input path to your directory");
                    String path = in.next();
                    System.out.println("Input path to your unzipped directory");
                    String pathTo = in.next();
                    f.unzipDirectory(path, pathTo);
                    break;
                }
            }
            System.out.println("Choose your action \n|| Input 1 for info || Input 2 for renameFile() method ||" +
                    " Input 3 for replaceFile() method ||\n|| Input 4 for filesEncounter() method || Input 5 for zipDirectory() method ||\n" +
                    "|| Input 6 for filesEncounter() method || Input 0 for exit || ");
            flag = in.next();

        }
    }
}
