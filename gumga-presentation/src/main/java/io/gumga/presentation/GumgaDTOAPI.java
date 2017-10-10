package io.gumga.presentation;

import java.io.Serializable;

public abstract class GumgaDTOAPI<T, ID extends Serializable> extends AbstractGumgaAPI<T, ID> {

	public GumgaDTOAPI(GumgaGateway<?, T, ID> gateway) {
		super(gateway);
	}
	
}
