package com.wnc.mymoney.backup.net;

import java.io.File;
import java.util.List;

public interface FilesZip
{
    public boolean zipAndShare(List<File> list, String destZip);

    public boolean zipAndSendEmail(List<File> list, String destZip);

}
