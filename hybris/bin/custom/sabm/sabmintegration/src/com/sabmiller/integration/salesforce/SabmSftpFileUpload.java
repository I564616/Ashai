/**
 *
 */
package com.sabmiller.integration.salesforce;

import java.io.File;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

/**
 * @author ramsatish.jagajyothi
 *
 */
public class SabmSftpFileUpload
{


	//MessageChannel sftpChannel;

	public void upload(final File file)
	{
		final Message<File> messageFile = MessageBuilder.withPayload(file).build();
		//sftpChannel.send(messageFile);
	}


//	public MessageChannel getSftpChannel() {
//		return sftpChannel;
//	}
//
//	public void setSftpChannel(final MessageChannel sftpChannel) {
//		this.sftpChannel = sftpChannel;
//	}

}
