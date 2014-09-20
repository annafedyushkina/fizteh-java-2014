package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.shell;


import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;
//import  java.nio.channels.FileChannel;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Shell {

    private static void remove(String whatDelete, String directory, boolean r) {
        File f = new File(directory + File.separator + whatDelete);
        if (!f.exists()) {
            System.out.println("rm: cannot remove \"" + f.getName() + "\": No such file or directory");
        }
        if (f.isFile()) {
            if (!f.delete()) {
                System.out.println("rm: cannot remove \"" + f.getName() + "\"");
            }
        }
        if (f.isDirectory()) {
            if (r == true) {
                String[] list;
                list = f.list();
                if (list.length == 0) {
                    System.out.println("rm: " + f.getName() + " is a directory");
                }
                for (String value : list) {
                    remove(value, directory + File.separator + whatDelete, r);
                }
                f.delete();
            } else {
                System.out.println("rm: " + f.getName() + " is a directory");
            }
        }
    }

    private static void copy(File f1, File f2, boolean r) {
        try {
            if (f1.isFile()) {
                if (!f2.exists()) {
                    if (!f2.mkdir()) {
                        System.out.println("mkdir: Cannot create new directory " + f2.getName());
                    }
                }
                InputStream is = null;
                OutputStream os = null;
                try {
                    is = new FileInputStream(f1);
                    File dest = new File(f2.getCanonicalPath() + File.separator + f1.getName());
                    if(dest.exists()) {
                        System.out.println(dest.getName() + "File exists.");
                        return;
                    }
                    os = new FileOutputStream(dest);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                } finally {
                    is.close();
                    os.close();
                }
                //Files.copy(f1.toPath(), f2.toPath(),);
            } else {
                if (r == true) {
                    if (!f2.exists()) {
                        if (!f2.mkdir()) {
                            System.out.println("mkdir: Cannot create new directory " + f2.getName());
                        }
                    }
                    File newDir = new File(f2.getCanonicalPath() + File.separator + f1.getName());
                    Files.createDirectory(newDir.toPath());
                    String[] list = f1.list();
                    for (String aList : list) {
                        copy(new File(f1, aList), newDir, r);
                    }
                } else {
                    System.out.println("cp: " + f1.getName() + " is a directory (not copied).");
                }
            }

        } catch (IOException e) {
            System.out.println("Exception on coping");
        }
    }

    public static void main(String[] args) {
        int i = 0;
        String directory = System.getProperty("user.home");
        String[] s;
        if (args.length == 0) {
            System.out.print("$ ");
            Scanner sc = new Scanner(System.in);
            String s1 = sc.nextLine();
            s = s1.split(" ");
        } else {
            s = args;
        }

        while (!s[i].equals("exit")) {
            while (i < s.length && !s[i].equals("exit")) {
                try {
                    switch (s[i]) {

                        case "cd": {
                            ++i;
                            String what;
                            int j = s[i].indexOf(';');
                            if (j != -1) {
                                what = s[i].substring(0, s[i].length() - 1);
                            } else {
                                what = s[i];
                            }
                            if (what.equals(".")) {
                                ++i;
                                break;
                            }
                            if (what.equals("..") ) {
                                if(!directory.equals("C:\\") && !directory.equals("/")) {
                                directory = new File(directory).getParent();
                                }
                                ++i;
                                break;
                            }
                            if (new File(what).exists()) {
                                directory = what;
                            } else {
                                if (new File(directory + File.separator + what).exists()) {
                                    directory = directory + File.separator + what;
                                } else {
                                    System.out.println("cd: \'" + what + "\': No such file or directory");
                                }
                            }
                            ++i;
                            break;
                        }

                        case "ls":
                        case "ls;": {
                            if (directory == null) {
                                System.out.println("Bad directory");
                                ++i;
                                break;
                            }
                            File path = new File(directory);
                            if (!path.isFile()) {
                                String[] list;
                                list = path.list();
                                for (String value : list) {
                                    System.out.println(value);
                                }
                            } else {
                                System.out.println("It is not a directory");
                            }
                            ++i;
                            break;
                        }

                        case "mkdir": {
                            ++i;
                            String dirname;
                            int j = s[i].indexOf(';');
                            if (j != -1) {
                                dirname = s[i].substring(0, s[i].length() - 1);
                            } else {
                                dirname = s[i];
                            }
                            File newDir = new File(directory + File.separator + dirname);
                            Files.createDirectory(newDir.toPath());
                            ++i;
                            break;
                        }

                        case "pwd":
                        case "pwd;": {
                            File f = new File(directory);
                            System.out.println(f.getCanonicalPath());
                            //System.out.println(f.getAbsolutePath());
                            ++i;
                            break;
                        }

                        case "mv": {
                            ++i;
                            String source, destination;
                            source = s[i];
                            ++i;
                            int j = s[i].indexOf(';');
                            if (j != -1) {
                                destination = s[i].substring(0, s[i].length() - 1);
                            } else {
                                destination = s[i];
                            }
                            File f1 = new File(directory + File.separator + source);
                            File f2 = new File(directory + File.separator + destination);
                            if ((f1.isDirectory() && f2.isDirectory() &&
                                    f1.getCanonicalPath().equals(f2.getCanonicalPath()))
                                    ||
                                    (f1.isFile() && f2.isFile() && f1.getCanonicalPath().equals(f2.getCanonicalPath()))
                                    || (f1.isDirectory() && f2.isFile() &&
                                    f1.getAbsolutePath().equals(f2.getCanonicalPath()))
                                    || (f1.isFile() && f2.isDirectory() &&
                                    f1.getCanonicalPath().equals(f2.getAbsolutePath()))
                                    ) {
                                f1.renameTo(f2);
                            } else {
                                Files.move(f1.toPath(), f2.toPath());
                            }
                            ++i;
                            break;
                        }

                        case "rm": {
                            ++i;
                            boolean flag = false;
                            if (s[i].equals("-r")) {
                                flag = true;
                                ++i;
                            }
                            String whatDelete;
                            int j = s[i].indexOf(';');
                            if (j != -1) {
                                whatDelete = s[i].substring(0, s[i].length() - 1);
                            } else {
                                whatDelete = s[i];
                            }
                            remove(whatDelete, directory, flag);
                            ++i;
                            break;
                        }

                        case "cp": {
                            ++i;
                            boolean flag = false;
                            if (s[i].equals("-r")) {
                                flag = true;
                                ++i;
                            }
                            String source, destination;
                            source = s[i];
                            ++i;
                            int j = s[i].indexOf(';');
                            if (j != -1) {
                                destination = s[i].substring(0, s[i].length() - 1);
                            } else {
                                destination = s[i];
                            }
                            File f1 = new File(directory + File.separator+ source);
                            File f2 = new File(directory + File.separator + destination);
                            copy(f1, f2, flag);

                            ++i;
                            break;
                        }

                        case "cat": {
                            ++i;
                            String name;
                            int j = s[i].indexOf(';');
                            if (j != -1) {
                                name = s[i].substring(0, s[i].length() - 1);
                            } else {
                                name = s[i];
                            }
                            File f = new File(directory + File.separator + name);
                            if (!f.exists()) {
                                System.out.println("cat: " + f.getName() + ": no such file");
                                ++i;
                                break;
                            }
                            if (f.isDirectory()) {
                                System.out.println("cat: " + f.getName() + ": it is a directory");
                                ++i;
                                break;
                            }
                            BufferedReader fin = new BufferedReader(new FileReader(f));
                            String line;
                            while ((line = fin.readLine()) != null) {
                                System.out.println(line);
                            }
                            ++i;
                            break;
                        }

                        default: {
                            //System.out.println("Error request");
                            ++i;
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Exception on coping : " + e.getMessage());
                }
                catch(Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            }
            if(i < s.length && s[i].equals("exit")){
                break;
            }
            i = 0;
            System.out.print("$ ");
            Scanner sc = new Scanner(System.in);
            String s1 = sc.nextLine();
            s = s1.split(" ");
        }
    }
}
