package com.sabmiller.webservice.importer;

public interface ContextSupportImportHandler<RequestEntity, Response,IC> extends ImportHandler<RequestEntity, Response> {
    Response importEntity(final RequestEntity entity,IC importContext);

   IC createImportContext();
}
