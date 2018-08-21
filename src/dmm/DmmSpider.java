package dmm;

import org.jsoup.nodes.Document;

import com.wnc.basic.BasicFileUtil;
import com.wnc.mymoney.uihelper.MyAppParams;
import com.wnc.mymoney.util.JsoupHelper;

public class DmmSpider {

	public static void run() {
		new DmmHotTask().start();
	}
}
