/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.justplainwiley.rawproperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * RawProperties provides a means of modifying a properties file while
 * preserving whitespace and comments. <br> No processing is performed on the
 * keys or values as they are read in, so escape sequences are preserved in
 * their "raw" state.<br> RawProperties also provides a List of Entries, which
 * allows you to insert new properties while still preserving multiline entries.
 * Multiline entries are also preserved in their original state when reading and
 * subsequently writing the properties file.
 *
 * @author Wiley Fuller <wiley.fuller@gmail.com>
 */
public class RawProperties implements Map<String, String> {

    private HashMap<String, RawEntry> entryMap = new HashMap<>();
    private ArrayList<RawEntry> entryList = new ArrayList<>();
    ArrayList<String> fileContent = new ArrayList<>();

    public RawProperties() {
    }

    public void load(InputStream is) throws UnsupportedEncodingException, IOException {

        try (InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr)) {

            String line = br.readLine();

            while (line != null) {
                fileContent.add(line);
                line = br.readLine();
            }
        }

        parseFileContent();

    }

    private void parseFileContent() {
        boolean inProp = false;
        if (fileContent.isEmpty()) {
            return;
        }
        RawEntry entry = new RawEntry();
        for (int i = 0; i < fileContent.size(); i++) {
            String line = fileContent.get(i);
            if (!inProp) {
                entry.setLineNum(i);
                int firstPos = 0;
                StringBuilder keyPaddingSb = new StringBuilder();
                for (; firstPos < line.length(); firstPos++) {
                    char c = line.charAt(firstPos);
                    if (c == '#' || c == '!') {
                        entry.setType(EntryType.COMMENT);
                        break;
                    } else if (Character.isWhitespace(c)) {
                        keyPaddingSb.append(c);
                        continue;
                    } else if (!Character.isWhitespace(c)) {
                        entry.setType(EntryType.PROPERTY);
//                        entry.setKey(getKeyFromProperty(line));
                        break;
                    }
                }
                entry.setKeyPadding(keyPaddingSb.toString());
                if (entry.getType() == EntryType.PROPERTY) {
                    int lastPos = firstPos;
                    for (; lastPos < line.length(); lastPos++) {
                        char c = line.charAt(lastPos);
                        if (Character.isWhitespace(c) || c == '=' || c == ':') {
                            entry.setSeparator(c);
                            break;
                        }
                    }
                    entry.setKey(line.substring(firstPos, lastPos));

                    int valPos = lastPos;
                    StringBuilder sepPaddingSb = new StringBuilder();
                    for (; valPos < line.length(); valPos++) {
                        char c = line.charAt(valPos);
                        if (Character.isWhitespace(c)) {
                            sepPaddingSb.append(c);
                            continue;
                        } else if (c == '=' || c == ':') {
                            //skip the first separator char
                            entry.setSeparator(c);
                            valPos++;
                            break;
                        } else {
                            break;
                        }
                    }
                    String sp = Character.isWhitespace(entry.getSeparator()) ?
                            sepPaddingSb.substring(1) : sepPaddingSb.toString();
                    entry.setSeparatorPadding(sp);
                    if (valPos == line.length()) {
                        entry.addLine("");
                    } else {
                        entry.addLine(line.substring(valPos));
                    }
                }
                if (entry.getType() == null) {
                    entry.setType(EntryType.BLANK_LINE);
                }
                entryList.add(entry);
            }
            if (entry.getType() == EntryType.PROPERTY) {
                if (!inProp) {
                    entryMap.put(entry.getKey(), entry);
                } else {
                    entry.addLine(line);
                }
                inProp = hasContinuation(line);
                if (inProp) {
                    continue;
                }
            } else {
                entry.addLine(line);
                inProp = false;
            }
            entry = new RawEntry();

        }

        // check if final entry has a continuation. if so, chop it off.
        if (entry.getType() == EntryType.PROPERTY && entry.getLines().size() > 0) {
            String last = entry.getLines().get(entry.getLines().size() - 1);
            if (hasContinuation(last)) {
                last = last.substring(0, last.length() - 1);
                entry.getLines().set(entry.getLines().size() - 1, last);
            }
        }
    }

    boolean hasContinuation(String value) {
        int backslashCount = 0;
        int pos = value.length() - 1;
        while (pos > 0 && value.charAt(pos) == '\\') {
            backslashCount++;
            pos--;
        }
        return (backslashCount != 0) && (backslashCount % 2 == 1);
    }

    public void store(Writer out) throws IOException {
        for (RawEntry entry : entryList) {
            if (entry.getType() == EntryType.COMMENT
                    || entry.getType() == EntryType.BLANK_LINE) {
                out.write(entry.getLines().get(0));
                out.write("\n");
            } else {
                out.write(entry.getKeyPadding());
                out.write(entry.getKey());
                out.write(entry.getSeparatorPadding());
                out.write(entry.getSeparator());
                for (String line : entry.getLines()) {
                    out.write(line);
                    out.write("\n");
                }
            }
        }
    }

    public RawEntry getEntry(String key) {
        return entryMap.get(key);
    }

    @Override
    public int size() {
        return entryMap.size();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String get(Object key) {
        return entryMap.get(key).getValue();
    }

    @Override
    public String put(String key, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String remove(Object key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<String> keySet() {
        return entryMap.keySet();
    }

    @Override
    public Collection<String> values() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    ArrayList<RawEntry> getEntryList() {
        return entryList;
    }

    void insertEntry(int lineNum, RawEntry insertedEntry) {
        insertedEntry.setLineNum(lineNum);
        int newPropLength = insertedEntry.getLines().size();
        int insertedPos = 0;
        for (; insertedPos < entryList.size(); insertedPos++ ) { 
            RawEntry entry = entryList.get(insertedPos);
            if (entry.getLineNum() >= lineNum) {
                entryList.add(insertedPos, insertedEntry);
                break;
            }
        }
        for (; insertedPos < entryList.size(); insertedPos++ ) { 
            RawEntry entry = entryList.get(insertedPos);
            entry.setLineNum(entry.getLineNum() + newPropLength);
        }
        entryMap.put(insertedEntry.getKey(), insertedEntry);
    }
}
