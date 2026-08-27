package com.plip.analytics.application.port.in;

import com.plip.analytics.application.port.in.dto.AgitSearchPageDto;

public interface SearchAgitsUseCase {

	AgitSearchPageDto search(String query, String sort, int page, int size);
}
