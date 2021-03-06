package com.velik.recommend.log;

import java.io.File;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

public class FileAccessLogTest {
	@Test
	public void test() throws Exception {
		FileAccessLog accessLog = new FileAccessLog("foo", false);

		long date1 = System.currentTimeMillis();
		long date2 = System.currentTimeMillis() + 10;

		accessLog.log(new DefaultAccess(1, 123, 456, 789, date1));
		accessLog.log(new DefaultAccess(2, 1234, 0x4567000000000000L, 790, date2));

		File file = new File("foo");

		accessLog.close();

		AccessLogReader reader = new AccessLogReader(file, 0);

		Iterator<Access> it = reader.iterator();

		Access access = it.next();
		Assert.assertEquals(1, access.getMajorId());
		Assert.assertEquals(123, access.getMinorId());
		Assert.assertEquals(456, access.getUserId());
		Assert.assertEquals(date1, access.getDate());

		access = it.next();
		Assert.assertEquals(2, access.getMajorId());
		Assert.assertEquals(1234, access.getMinorId());
		Assert.assertEquals(0x4567000000000000L, access.getUserId());
		Assert.assertEquals(date2, access.getDate());

		Assert.assertFalse(it.hasNext());

		file.delete();
	}

	@Test
	public void testPerformance() {
		FileAccessLog accessLog = new RotatingFileAccessLog("foo", false);

		long t = System.currentTimeMillis();

		for (int i = 0; i < 100000; i++) {
			accessLog.log(new DefaultAccess(1, 123, 456, 789, t));
		}

		accessLog.close();

		long msElapsed = System.currentTimeMillis() - t;

		if (msElapsed > 10000) {
			Assert.fail(msElapsed + " ms per 100,000 logs is too slow.");
		}

		System.out.println(msElapsed + " ms for 100,000 logs.");
	}
}
