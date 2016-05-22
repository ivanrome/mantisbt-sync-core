/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jérard Devarulrajah
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.jrrdev.mantisbtsync.core.jobs.common;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.jrrdev.mantisbtsync.core.common.auth.PortalAuthBuilder;
import com.github.jrrdev.mantisbtsync.core.common.auth.PortalAuthManager;

/**
 * Configuration for the tasklets used by all jobs.
 *
 * @author jrrdev
 *
 */
@Configuration
public class CommonTasklets {

	/**
	 * Build the tasklet executing the portal authentication.
	 *
	 * @param authManager
	 * 		the portal authentication manager
	 * @return the tasklet executing the portal authentication
	 */
	@Bean
	@StepScope
	public MethodInvokingTaskletAdapter authTasklet(final PortalAuthManager authManager) {
		final MethodInvokingTaskletAdapter authTasklet = new MethodInvokingTaskletAdapter();
		authTasklet.setTargetObject(authManager);
		authTasklet.setTargetMethod("authentificate");
		return authTasklet;
	}

	@Bean
	@JobScope
	public PortalAuthBuilder authBuilder() {
		return new PortalAuthBuilder();
	}

	@Bean
	@JobScope
	public PortalAuthManager authManager(@Value("${mantis.auth.filepath:}") final String filepath,
			final PortalAuthBuilder authBuilder) throws JAXBException, IOException {
		return authBuilder.buildAuthManager(filepath);
	}
}
