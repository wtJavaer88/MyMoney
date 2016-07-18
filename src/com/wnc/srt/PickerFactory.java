package com.wnc.srt;

public class PickerFactory
{
    public static Picker getPicker(String srtFile)
    {
        if (srtFile.endsWith(".srt"))
        {
            return new SrtPicker(srtFile);
        }
        else if (srtFile.endsWith(".ass"))
        {
            return new AssPicker(srtFile);
        }
        return null;
    }
}
