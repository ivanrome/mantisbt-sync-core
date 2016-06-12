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
package com.github.jrrdev.mantisbtsync.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.jrrdev.mantisbtsync.core.common.CommonConfiguration;

/**
 * Entry point for the application.
 *
 * @author jrrdev
 *
 */
@Configuration
@EnableCaching
@EnableAutoConfiguration
@Import(CommonConfiguration.class)
@ComponentScan({
	"com.github.jrrdev.mantisbtsync.core.jobs.common",
	"com.github.jrrdev.mantisbtsync.core.jobs.enums",
	"com.github.jrrdev.mantisbtsync.core.jobs.projects",
	"com.github.jrrdev.mantisbtsync.core.jobs.issues",
	"com.github.jrrdev.mantisbtsync.core.services"
})
public class Application {

	/**
	 * Entry point method.
	 *
	 * @param args
	 * 			Command line arguments
	 */
	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
