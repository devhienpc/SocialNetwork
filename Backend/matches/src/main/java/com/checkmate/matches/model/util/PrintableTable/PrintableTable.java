// Nicholas Donahue 2023
package com.checkmate.matches.model.util.PrintableTable;

import java.util.ArrayList;

public class PrintableTable { // printable table
    private final ArrayList<String> Headers; // column titles
    private final ArrayList<ArrayList<String>> Rows; // data ( rows )
    private int Padding; // padding is the space on each side


    // IMPLEMENTATION OF CRUD functions for our table's data.
    public void createHeaders(String... args) {
        // add a lot of headers at once.
        for (String i : args) {
            createHeader(i);
        }
    }
    public void createHeader(String headerName) { // easily add headers
        Headers.add(headerName);
    }
    public void updateHeader(String headerName, String newHeader) {
        for (int i = 0; i < Headers.size(); i++) {
            if (Headers.get(i).equals(headerName)) {
                Headers.set(i, newHeader);
            } else {
                System.out.println("String headerName must be spelled correctly.");
            }

        }
    }
    public void updateHeader(int index, String newHeader) {
        Headers.set(index, newHeader);
    }
    public void deleteHeader(int index) {
        Headers.remove(index);
    }
    public void deleteHeader(String headerName) {
        for (int i = 0; i < Headers.size(); i++) {
            if (Headers.get(i).equals(headerName)) {
                Headers.remove(i);
            } else {
                System.out.println("String headerName must be spelled correctly.");
            }

        }
    }

    public void createRows(ArrayList<String>... args) {
        // create multiple rows
        for (int i = 0; i < args.length; i++) {
            Rows.add(args[i]);
        }
    }

    public void createRow(String... args) {
        // create single row
        if (args.length != Headers.size()) {
            System.out.println("# of Arguments must equal # of Headers.");
            return;
        }

        ArrayList<String> thisRow = new ArrayList<String>();

        for (String s : args) {
            thisRow.add(s);
        }

        Rows.add(thisRow);
    }
    public void createRow(ArrayList<String> rowData) {
        if (rowData.size() != Headers.size()) {
            System.out.println("# of Arguments must equal # of Headers.");
            return;
        }
        Rows.add(rowData);
    }

    public void updateRow(int index, ArrayList<String> rowData) {
        Rows.set(index, rowData);
    }

    public void deleteRow(int index) {
        Rows.remove(index);
    }

    public void print() {
        // Prints the table to the console

        ArrayList<Integer> columnSize = new ArrayList<Integer>(); // array list to hold the length of the longest string in each column

        for (int i = 0; i < Headers.size(); i++)  { // for loop to get the length of each header
            columnSize.add(Headers.get(i).length());
        }
        
        for (ArrayList<String> Row : Rows) { // for loop to get each row of data
            for (int i = 0; i < Row.size(); i++) { // loop through columns
                int len = Row.get(i).length();  // getting the length of data in this column
                if (len > columnSize.get(i)) { // comparing this length to the longest recorded length so far.
                    columnSize.set(i, len);  // updating the longest recorded lenght to this length.
                }
            }
        }

        int totalSize = Headers.size() + 1; // total length of the table
        for (int i = 0; i < columnSize.size(); i++) {
            columnSize.set(i, columnSize.get(i) + (Padding * 2)); // adding padding ( to both sides) for our longest column sizes
            totalSize += columnSize.get(i); // updating the total length of the table
        }

        for (int i = 0; i < totalSize; i++) { System.out.print("-"); } // close the top of the box

        System.out.println();
        System.out.print("|");

        for (int dataIndex = 0; dataIndex < Headers.size(); dataIndex++) {
            String data = Headers.get(dataIndex);

            int dataSize = data.length();
            int longest = columnSize.get(dataIndex);

            int diff = longest - dataSize;

            if (diff % 2 == 0) {
                for (int i = 0; i < diff / 2; i++) {
                    System.out.print(" ");
                }

                System.out.print(data);
                for (int i = 0; i < diff / 2; i++) {
                    System.out.print(" ");
                }
            } else {
                for (int i = 0; i < Math.floor((double) (diff) / 2.0); i++) {
                    System.out.print(" ");
                }

                System.out.print(data);
                for (int i = 0; i < Math.ceil((double) (diff) / 2.0); i++) {
                    System.out.print(" ");
                }
            }


            System.out.print("|");
        }
        System.out.println();

        for (int RowIndex = 0; RowIndex < Rows.size(); RowIndex++) {
            ArrayList<String> Row = Rows.get(RowIndex);

            System.out.print("|");
            for (int dataIndex = 0; dataIndex < Row.size(); dataIndex++)  {
                String data = Row.get(dataIndex);

                int dataSize = data.length();
                int longest = columnSize.get(dataIndex);

                int diff = longest - dataSize;

                if (diff % 2 == 0) {
                    for (int i = 0; i < diff / 2; i++) {
                        System.out.print(" ");
                    }

                    System.out.print(data);
                    for (int i = 0; i < diff / 2; i++) {
                        System.out.print(" ");
                    }
                } else {
                    for (int i = 0; i < Math.floor((double) (diff) / 2.0); i++) {
                        System.out.print(" ");
                    }

                    System.out.print(data);
                    for (int i = 0; i < Math.ceil((double) (diff) / 2.0); i++) {
                        System.out.print(" ");
                    }
                }

                System.out.print("|");
            }
            System.out.println();
        }
        for (int i = 0; i < totalSize; i++) { System.out.print("-"); } // close the bottom

        System.out.println();
    }
    public void setPadding(int p) {
        Padding = p;
    }
    public PrintableTable() { // initialize the printable table class
        Headers = new ArrayList<String>();
        Rows = new ArrayList<ArrayList<String>>();
        Padding = 1; //
    }
}
