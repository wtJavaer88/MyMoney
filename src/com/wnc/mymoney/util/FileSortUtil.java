package com.wnc.mymoney.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileSortUtil
{
    public static List<File> getSortFiles(File[] listFiles)
    {
        List<File> fileList = Arrays.asList(listFiles);
        Collections.sort(fileList, new Comparator<File>()
        {
            @Override
            public int compare(File o1, File o2)
            {
                if (o1.isDirectory() && o2.isFile())
                {
                    return 1;
                }
                if (o1.isFile() && o2.isDirectory())
                {
                    return -1;
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
        return fileList;
    }
}
