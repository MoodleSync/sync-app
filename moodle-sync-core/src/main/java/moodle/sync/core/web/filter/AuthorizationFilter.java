/*
 * Copyright (C) 2021 TU Darmstadt, Department of Computer Science,
 * Embedded Systems and Applications Group.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package moodle.sync.core.web.filter;

import com.google.api.client.auth.oauth2.Credential;
import moodle.sync.core.fileserver.panopto.PanoptoException;
import moodle.sync.core.web.model.TokenProvider;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static moodle.sync.core.fileserver.panopto.util.PanoptoAuthorizer.authorize;

import java.io.IOException;
import java.util.concurrent.*;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthorizationFilter implements ClientRequestFilter {

    private static final String BEARER = "Bearer ";


    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        TokenProvider tokenProvider = (TokenProvider) requestContext
                .getConfiguration().getProperty(TokenProvider.class.getName());

        if (isNull(tokenProvider)) {
            return;
        }

        String apiKey = tokenProvider.getApiKey();
        String apiSecret = tokenProvider.getApiSecret();

        if (nonNull(apiKey) && nonNull(apiSecret)) {
            Credential token = authorize(apiKey, apiSecret);
            requestContext.getHeaders().putSingle(AUTHORIZATION, BEARER + token.getAccessToken());
        }
    }
}
