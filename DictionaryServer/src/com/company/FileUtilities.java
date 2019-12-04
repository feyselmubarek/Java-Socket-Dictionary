//1.Ermiyas Gezahegn… ATR /5552/09 ermiyas10080@gmail.com
// 2.Fasil Beshiwork … ATR/9359/09 …. fasilbeshiwork17@gmail.com
// 3. Feysel Mubarek … ATR/5064/09…feyselmubarek@gmail.com
// 4.Habte Assefa… ATR/0081/09…. habteasefa726@gmail.com
// 5. Hana Tesfaye.…. ATR/4224/09…. hanatesfaye223@gmail.com

package com.company;

import java.io.*;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class FileUtilities {
    public static Dictionary<String, String> geek = new Hashtable<String, String>();

    public String populateTable() throws IOException {
        File f = new File("test.txt");

        if(!f.exists()){ f.createNewFile(); }
        BufferedReader br = new BufferedReader(new FileReader(f));

        String st;
        while ((st = br.readLine()) != null) {
            String array[] = st.split("`");
            geek.put(array[0], array[1]);
        }

        br.close();

        for (Enumeration<String> i = geek.elements(); i.hasMoreElements();) {
            System.out.println("Value in Dictionary : " + i.nextElement());
        }

        String data = "";
        // keys() method :
        for (Enumeration<String> k = geek.keys(); k.hasMoreElements();) {
            String word = k.nextElement();
            String meaning = geek.get(word);
            data += word + "`" + meaning + "~";
        }
        System.out.println(data);
        return data;
    }

    public String addWord(String key, String value) {
        String status = "Success";
        try {
            if (geek.get(key) == null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            geek.put(key, value);
                        }
                    }
                }).start();
            } else {
                status = "Duplicate";
            }
        } catch (Exception e) {
            status = "Error";
        }
        return status;
    }

    public String addDefinition(String key, String value) {
        String status = "Multiple";
        try {
            if (geek.get(key) != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            String val = geek.get(key);
                            val += "@" + value;
                            System.out.println("Multiple " + val);
                            geek.put(key, val);
                        }
                    }
                }).start();
            } else {
                status = "NotFound";
            }
        } catch (Exception e) {
            status = "Error";
        }
        return status;
    }

    public String get(String key) {
        String status = "Error";
        try {
            if (geek.get(key) != null) {
                status = key;
            }
        } catch (Exception e) {
        }
        return status;
    }

    public String removeWord(String key) throws Exception {
        String status = "Success";
        try {
            if (geek.get(key) != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            geek.remove(key);
                        }
                    }
                }).start();
            } else {
                status = "Error";
            }
        } catch (Exception e) {
            status = "Error";
        }
        return status;
    }

    public String getDictionaryData() throws IOException {
        if (geek.isEmpty()) {
            return populateTable();
        }
        String data = "";
        // keys() method :
        for (Enumeration<String> k = geek.keys(); k.hasMoreElements();) {
            String word = k.nextElement();
            String meaning = geek.get(word);
            data += word + "`" + meaning + "~";
        }
        return data;
    }
}
