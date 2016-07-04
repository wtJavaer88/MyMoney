package train.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;

import com.wnc.basic.BasicStringUtil;

public class HttpsConnUtil
{
    // private static final String SYS_VULLN_URL_JSON =
    // "https://10.65.80.34/api/template/webvuln/list?username=admin&password=nsfocus123";
    private static final String SYS_VULLN_URL_JSON = "https://kyfw.12306.cn/otn/leftTicket/query?leftTicketDTO.train_date=2016-05-16"
            + "&leftTicketDTO.from_station=HSN&leftTicketDTO.to_station=WHN&purpose_codes=ADULT";

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier()
    {

        @Override
        public boolean verify(String hostname, SSLSession session)
        {
            return true;
        }
    };

    public static String httpGet(final String urlSite)
    {
        StringBuffer tempStr = new StringBuffer();
        String responseContent = "";
        HttpURLConnection conn = null;
        try
        {
            // Create a trust manager that does not validate certificate chains
            trustAllHosts();

            URL url = new URL(urlSite);

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            System.out.println(conn.getResponseCode() + " "
                    + conn.getResponseMessage());
            InputStream in = conn.getInputStream();
            conn.setReadTimeout(10 * 1000);
            BufferedReader rd = new BufferedReader(new InputStreamReader(in,
                    "UTF-8"));
            String tempLine;
            while ((tempLine = rd.readLine()) != null)
            {
                tempStr.append(tempLine);
            }
            responseContent = tempStr.toString();
            System.out.println(responseContent);
            rd.close();
            in.close();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return responseContent;
    }

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts()
    {

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]
        { new X509TrustManager()
        {

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers()
            {
                return new java.security.cert.X509Certificate[]
                {};
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                    String authType)
            {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                    String authType)
            {

            }
        } };

        // Install the all-trusting trust manager
        try
        {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        httpGet(SYS_VULLN_URL_JSON);
    }

    public static String requestHTTPSPage(Activity context, String mUrl)
            throws Exception
    {
        if (BasicStringUtil.isNullString(mUrl))
        {
            return "";
        }
        InputStream ins = null;
        String result = "";
        try
        {
            ins = context.getAssets().open("srca.cer"); // 下载的证书放到项目中的assets目录中
            CertificateFactory cerFactory = CertificateFactory
                    .getInstance("X.509");
            Certificate cer = cerFactory.generateCertificate(ins);
            KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
            keyStore.load(null, null);
            keyStore.setCertificateEntry("trust", cer);

            SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
            Scheme sch = new Scheme("https", socketFactory, 443);
            HttpClient mHttpClient = new DefaultHttpClient();
            mHttpClient.getConnectionManager().getSchemeRegistry()
                    .register(sch);

            BufferedReader reader = null;
            try
            {
                HttpGet request = new HttpGet();
                request.setURI(new URI(mUrl));
                HttpResponse response = mHttpClient.execute(request);
                if (response.getStatusLine().getStatusCode() != 200)
                {
                    request.abort();
                    return result;
                }

                reader = new BufferedReader(new InputStreamReader(response
                        .getEntity().getContent()));
                StringBuffer buffer = new StringBuffer();
                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    buffer.append(line);
                }
                result = buffer.toString();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (reader != null)
                {
                    reader.close();
                }
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        finally
        {
            try
            {
                if (ins != null)
                {
                    ins.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

}
