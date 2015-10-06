package bigclaw;

import java.net.HttpURLConnection;
import java.net.URL;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;


public class MgpyhPageProcessor implements PageProcessor{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        Spider.create(new MgpyhPageProcessor())
        //从"推荐页第一页"开始抓
        .addUrl("http://www.mgpyh.com/post/?page=1")
        .addPipeline(new MgpyhPipeline())
        //开启1个线程抓取
        .thread(1)
        //启动爬虫
        .run();
	}

	public void process(Page page) {
		// TODO Auto-generated method stub
		

		
		if(page.getUrl().toString().contains("recommend")) //如果是详细也，获取内容
		{
			//title /html/body/div[1]/div[2]/div/div[1]/h1/text()			
			page.putField("title", page.getHtml().xpath("/html/body/div[1]/div[2]/div/div[1]/h1/text()").toString());
			
			///catagory html/body/div[1]/div[2]/div/div[1]/div[1]/a[1]
			page.putField("catagory", page.getHtml().xpath("html/body/div[1]/div[2]/div/div[1]/div[1]/a[1]/text()").toString());
			
			//img /html/body/div[1]/div[2]/div/div[1]/div[2]/div/div/img
			page.putField("img", page.getHtml().xpath("/html/body/div[1]/div[2]/div/div[1]/div[2]/div/div/img/@src").toString());			
			
			//selllink /html/body/div[1]/div[2]/div/div[1]/div[2]/div/div/a
			String selllink= page.getHtml().xpath("/html/body/div[1]/div[2]/div/div[1]/div[2]/div/div/a/@href").toString();
			page.putField("selllink", selllink);	
			
			page.putField("realselllink", getRealselllink(selllink));
			//page.addTargetRequests(page.getHtml().xpath("/html/body/div[1]/div[2]/div/div[1]/div[2]/div/div").links().regex("(http://www\\.mgpyh\\.com/goods/\\w+/)").all());
			
			//content /html/body/div[1]/div[2]/div/div[1]/div[2]/div
			page.putField("content", page.getHtml().xpath("/html/body/div[1]/div[2]/div/div[1]/div[2]/div").toString());
 		}
		else if(page.getUrl().toString().contains("post"))//如果是列表页，则只取链接
		{
		      page.addTargetRequests(page.getHtml().xpath("/html/body/div[1]/div[2]/div/div[1]/div[2]").links().regex("(http://www\\.mgpyh\\.com/recommend/\\d+/)").all());
		      //还可以继续读取别的翻页后的链接

		}
		
	}

    public String getRealselllink(String url) {  
    	String location ="";
    	try {  
           // System.out.println("访问地址:" + url);  
            URL serverUrl = new URL(url);  
            HttpURLConnection conn = (HttpURLConnection) serverUrl  
                    .openConnection();  
            conn.setRequestMethod("GET");  
            // 必须设置false，否则会自动redirect到Location的地址  
            conn.setInstanceFollowRedirects(false);  
  
            conn.addRequestProperty("Accept-Charset", "UTF-8;");  
            conn.addRequestProperty("User-Agent",  
                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");  
            conn.addRequestProperty("Referer", "http://zuidaima.com/");  
            conn.connect();  
            location = conn.getHeaderField("Location");  
  
           // System.out.println("跳转地址:" + location);  
			//return location;
  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return location;
        
    }  
	
	
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

}
