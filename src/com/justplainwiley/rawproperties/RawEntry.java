/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.justplainwiley.rawproperties;

import java.util.ArrayList;

/**
 *
 * @author wiley
 */
class RawEntry {

    private char separator = '=';
    private String separatorPadding = "";
    private EntryType type;
    private ArrayList<String> lines = new ArrayList<>(1);
    private String key = "";
    private String keyPadding = "";
    private int lineNum = 0;

    public EntryType getType() {
        return type;
    }

    public void setType(EntryType type) {
        this.type = type;
    }

    void addLine(String line) {
        getLines().add(line);
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the lineNum
     */
    public int getLineNum() {
        return lineNum;
    }

    /**
     * @param lineNum the lineNum to set
     */
    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    /**
     * @return the value
     */
    public String getValue() {
        String value = "";
        if (type == EntryType.PROPERTY) {
            if (getLines().size() == 1) {
                return trimLeadingWhitespace(getLines().get(0));
            } else {
                for (int i = 0; i < getLines().size(); i++) {
                    value += trimLeadingWhitespace(getLines().get(i));
                    if (i != getLines().size() - 1) {
                        value = chopSlashes(value);
                    }
                }
            }
        }
        return value;
    }

    /**
     * @return the separator
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * @param separator the separator to set
     */
    public void setSeparator(char separator) {
        this.separator = separator;
    }

    private String chopSlashes(String line) {
        int pos = line.length() - 1;
        while (pos > 0 && line.charAt(pos) == '\\') {
            pos--;
        }
        pos++;
        return line.substring(0, pos);
    }

    private String trimLeadingWhitespace(String line) {
        int pos = 0;
        for (int i = 0; i < line.length() && Character.isWhitespace(line.charAt(i)); i++) {
            pos = i + 1;
        }
        if (pos > 0) {
            return line.substring(pos);
        } else {
            return line;
        }
    }

    /**
     * @return the lines
     */
    public ArrayList<String> getLines() {
        return lines;
    }

    /**
     * @param lines the lines to set
     */
    public void setLines(ArrayList<String> lines) {
        this.lines = lines;
    }

    /**
     * @return the keyPadding
     */
    public String getKeyPadding() {
        return keyPadding;
    }

    /**
     * @param keyPadding the keyPadding to set
     */
    public void setKeyPadding(String keyPadding) {
        this.keyPadding = keyPadding;
    }

    /**
     * @return the separatorPadding
     */
    public String getSeparatorPadding() {
        return separatorPadding;
    }

    /**
     * @param separatorPadding the separatorPadding to set
     */
    public void setSeparatorPadding(String separatorPadding) {
        this.separatorPadding = separatorPadding;
    }
}
