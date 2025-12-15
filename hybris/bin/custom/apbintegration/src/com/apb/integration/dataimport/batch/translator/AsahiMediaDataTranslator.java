package com.apb.integration.dataimport.batch.translator;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.media.MediaDataTranslator;
import de.hybris.platform.jalo.Item;

public class AsahiMediaDataTranslator extends MediaDataTranslator{
		
	@Override
	public void performImport(String cellValue, Item processedItem) throws ImpExException {
		MediaDataTranslator.setMediaDataHandler(new AsahiMediaDataHandler());
		super.performImport(cellValue, processedItem);
		MediaDataTranslator.unsetMediaDataHandler();
		/*
		 * if (cellValue != null && cellValue.length() > 0 && processedItem != null &&
		 * processedItem.isAlive()) { handler.importData((Media)processedItem,
		 * cellValue); } else { if (LOG.isDebugEnabled()) { LOG.debug((
		 * Object)"Can not import data to media cause processed item is not alive "); }
		 * }
		 */
    }
	

}
