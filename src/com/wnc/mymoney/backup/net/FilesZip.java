package com.wnc.mymoney.backup.net;

import java.io.File;
import java.util.Collection;

public interface FilesZip
{
    public boolean zipAndShare(Collection<File> files, String destZip);

    public boolean zipAndSendEmail(Collection<File> files, String destZip);

    public boolean zipAndSendEmailBackGround(Collection<File> list,
            String destZip);
}
