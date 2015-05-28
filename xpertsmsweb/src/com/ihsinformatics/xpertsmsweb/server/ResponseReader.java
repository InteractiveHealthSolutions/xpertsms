package com.ihsinformatics.xpertsmsweb.server;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.irdresearch.smstarseel.context.TarseelContext;
import org.irdresearch.smstarseel.context.TarseelServices;
import org.irdresearch.smstarseel.data.InboundMessage;
import org.irdresearch.smstarseel.data.InboundMessage.InboundStatus;

import com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResults;

public class ResponseReader extends TimerTask {

	@Override
	public void run() {
		TarseelServices tsc = TarseelContext.getServices();
		String sender = null;
		String text = null;
		try {
			List<InboundMessage> list = tsc.getSmsService().findInbound(null,
					null, InboundStatus.UNREAD, null, null, null,
					tsc.getDeviceService().findProjectById(1).getProjectId(),
					false);

			System.out.println("Running Job: ResponseReaderJob " + new Date()
					+ ". Fetched " + list.size() + " UNREAD sms");

			for (InboundMessage ib : list) {
				sender = ib.getOriginator();
				text = ib.getText();

				GeneXpertResults gxp = parseText(text);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public GeneXpertResults parseText(String text) {
		GeneXpertResults gxp = new GeneXpertResults();
		// String[] tokens = text.split("\\^");
		return gxp;

	}

}
