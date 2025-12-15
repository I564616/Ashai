package com.sabmiller.facades.customer.converters.populators;

import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.core.util.SabmNumberUtils;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.facades.invoice.SABMInvoiceDiscrepancyData;
import com.sabmiller.facades.invoice.SABMInvoiceItemData;
import com.sabmiller.integration.sap.invoices.discrepancy.response.InvoiceItemDataResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by zhuo.a.jiang on 10/8/18.
 */
public class InvoiceItemDataRestPopulator implements Populator<InvoiceItemDataResponse.Invoice, SABMInvoiceDiscrepancyData> {



    private  final String DATE_PATTERN = "ddMMyyyy";

    private  final String DATE_PATTERN_ToBe = "dd/MM/yyyy";

    private static final Logger LOG = Logger.getLogger(InvoiceItemDataRestPopulator.class);

    /**
     * Populate the target instance with values from the source instance.
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(InvoiceItemDataResponse.Invoice source, SABMInvoiceDiscrepancyData target) throws ConversionException {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        target.setSoldTo(source.getSoldTo());
        target.setInvoiceNumber(source.getInvoiceNumber());


        List<InvoiceItemDataResponse.Invoice.Item> array1 = source.getItem();
        List<SABMInvoiceItemData> array2 = new ArrayList<SABMInvoiceItemData>();

        Double totalFreightAmount = 0.0;

        for (InvoiceItemDataResponse.Invoice.Item item : array1){

            SABMInvoiceItemData data = new SABMInvoiceItemData();

            data.setItemID(item.getItemID());

            if (StringUtils.contains(item.getItemDescription(),"|")){

              String [] array =  StringUtils.split(item.getItemDescription(),"|");

                data.setItemDescriptionLine1(array[0]);
                data.setItemDescriptionLine2(array[1]);

            }

            else {
                data.setItemDescriptionLine1(item.getItemDescription());
            }
            //material code or ean code
            data.setMaterial(item.getMaterial());
            data.setQuantity(item.getQuantity());
            data.setUoM(item.getUoM());
            data.setUnitPrice(item.getUnitPrice());
            if(StringUtils.isEmpty(item.getDiscount())){
                data.setDiscount("0.0");
            }
			else if (isStringOnlyAlphabet(item.getDiscount()))
			{
				data.setDiscount(item.getDiscount());
			}
            else {

               String discountPerSingleQuantity = SabmNumberUtils.formattingDouble(SabmStringUtils.sanitizeDoubleStringFromSAP(item.getDiscount()) / Double.valueOf(item.getQuantity()));

               data.setDiscount(discountPerSingleQuantity);
            }

            data.setAmount(item.getAmount());
            data.setContainerDeposit(item.getContainerDeposit());
            data.setWet(item.getWet());
            data.setLocalFreight(item.getLocalFreight());
            data.setGst(item.getGST());
            data.setTotalExGST(item.getTotalExGST());
            data.setLucExGST(item.getLUCExGST());
            array2.add(data);

            // assume that each item has same local Freight fee


            totalFreightAmount = totalFreightAmount+Double.valueOf(item.getLocalFreight());
        }

        target.setFreightChargedAmount(SabmNumberUtils.formattingDouble(totalFreightAmount));

        target.setInvoices(array2);

        //populator from  DDMMYYY to DD/MM/YYYY

        try {
            target.setInvoiceDate(SabmDateUtils.convertSimpleDateFormat(DATE_PATTERN_ToBe, DATE_PATTERN, source.getInvoiceDate()));
        } catch (ParseException e) {
            LOG.error("Date format parse error:  "+ source.getInvoiceDate());
        }

    }
	private boolean isStringOnlyAlphabet(final String bonusStock)
	{
		return ((bonusStock != null) && (!bonusStock.equals("")) && (bonusStock.matches("^[a-zA-Z ]*$")));

	}
}
