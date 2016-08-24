package cn.com.fastweb.fastmedia.demo;

import java.util.Locale;

class Samples {

	public static class Sample {

		public final String name;
		public final String contentId;
		public final String provider;
		public final String uri;

		public Sample(String name, String uri) {
			this(name, name.toLowerCase(Locale.US).replaceAll("\\s", ""), "",uri);
		}

		public Sample(String name, String contentId, String provider, String uri) {
			this.name = name;
			this.contentId = contentId;
			this.provider = provider;
			this.uri = uri;
		}

	}

	public static final Sample[] MISC = new Sample[] {
			new Sample("Fastweb Test Stream",
					"http://httpflv.fastweb.com.cn.cloudcdn.net/live_fw/stream"),
			new Sample("其它测试流",
					"http://fms.cntv.lxdns.com/live/flv/channel84.flv"), 
			new Sample("302跳转测试地址", "http://302.myxns.net/")
	};
	
	private Samples() {
	}

}
