package dmm;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wnc.basic.BasicFileUtil;
import com.wnc.mymoney.uihelper.MyAppParams;
import com.wnc.mymoney.util.JsoupHelper;
import com.wnc.mymoney.util.PatternUtil;

public class DmmHotTask extends Thread {
	public final String ERR_TXT = MyAppParams.getInstance().getLocalLogPath()
			+ "err.txt";
	public final String LOG_TXT = MyAppParams.getInstance().getLocalLogPath()
			+ "log.txt";

	private long time = 0L;
	@Override
	public void run() {
		super.run();
		time = System.currentTimeMillis();
		// getMp4Url("mide00191");
		for (int i = 1; i <= 417; i++) {
			getPageContent(i);
		}
	}

	private void getPageContent(int page) {
		try {
			Document documentResult = JsoupHelper
					.getDocumentResult("http://www.dmm.co.jp/litevideo/-/list/=/article=keyword/sort=all_ranking/page="
							+ page + "/");
			Elements select = documentResult.select("#list > li > div");

			for (Element element : select) {
				String url = element.select("p > a").attr("href");
				String count = element.select("div.value").text()
						.replaceAll("[,回再生]", "");
				String cidTxt = MyAppParams.getInstance().getLocalLogPath()
						+ "cid.txt";
				BasicFileUtil.writeFileString(cidTxt, url + " " + count
						+ "\r\n", null, true);

				getMp4Url(PatternUtil.getLastPatternGroup(url, "cid=(.+)/"));

			}
			if (select.size() == 0) {
				BasicFileUtil.writeFileString(ERR_TXT,
						page + "页沒有内容!" + "\r\n", null, true);
			} else {
				BasicFileUtil.writeFileString(LOG_TXT, page
						+ "  getPageContent 结束,总耗时(秒):"+(System.currentTimeMillis() - time)/1000 + "\r\n", null, true);
			}
			Thread.sleep(2000);
		} catch (Exception e) {
			BasicFileUtil.writeFileString(ERR_TXT, page
					+ "  getPageContent - err:" + e.getMessage() + "\r\n",
					null, true);
			e.printStackTrace();
		}
	}

	public void getMp4Url(String cid) {
		try {
			Document documentResult = JsoupHelper
					.getDocumentResult("http://www.dmm.co.jp/service/digitalapi/-/html5_player/=/cid="
							+ cid
							+ "/mtype=AhRVShI_/service=litevideo/mode=/width=560/height=360/");
//			System.out.println(documentResult);
			String docStr = documentResult.toString();
			List<String> allPatternGroup = PatternUtil.getAllPatternGroup(
					docStr, "\"src\":\"(.*?)\"");
			String videosTxt = MyAppParams.getInstance().getLocalLogPath()
					+ "videos.txt";
			String videosUniTxt = MyAppParams.getInstance().getLocalLogPath()
					+ "videos-uni.txt";
			String videosUniLowTxt = MyAppParams.getInstance()
					.getLocalLogPath() + "videos-uni-low.txt";
			String last = null;
			// 保留所有视频源
			for (String string : allPatternGroup) {
				// 简单去重
				if (string.equals(last)) {
					continue;
				}
				last = string;
				// System.out.println(string.replace("\\", ""));
				BasicFileUtil.writeFileString(videosTxt,
						string.replace("\\", "") + "\r\n", null, true);
			}
			// 保留唯一副本
			if (allPatternGroup.size() > 0) {
				BasicFileUtil.writeFileString(videosUniLowTxt, allPatternGroup
						.get(0).replace("\\", "") + "\r\n", null, true);
				BasicFileUtil.writeFileString(videosUniTxt, allPatternGroup
						.get(allPatternGroup.size() - 1).replace("\\", "")
						+ "\r\n", null, true);
			}

			else {
				BasicFileUtil.writeFileString(ERR_TXT, cid + " 沒有視頻!" + "\r\n",
						null, true);
			}
		} catch (Exception e) {
			BasicFileUtil.writeFileString(ERR_TXT, cid + " getMp4Url - err:"
					+ e.getMessage() + "\r\n", null, true);
			e.printStackTrace();
		}
	}
}
