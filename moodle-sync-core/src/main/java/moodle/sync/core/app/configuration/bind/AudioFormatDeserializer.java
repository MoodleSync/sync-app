/*
 * Copyright (C) 2020 TU Darmstadt, Department of Computer Science,
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

package moodle.sync.core.app.configuration.bind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import moodle.sync.core.audio.AudioFormat;
import moodle.sync.core.audio.AudioFormat.Encoding;

import java.io.IOException;

/**
 * Implementation of an {@link AudioFormat} JSON deserializer.
 *
 * @author Alex Andres
 */
public class AudioFormatDeserializer extends JsonDeserializer<AudioFormat> {

	@Override
	public AudioFormat deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		JsonNode node = parser.getCodec().readTree(parser);

		AudioFormat format;

		try {
			int samplerate = node.get("samplerate").intValue();
			int channels = node.get("channels").intValue();
			Encoding encoding = Encoding.valueOf(node.get("encoding").textValue());

			format = new AudioFormat(encoding, samplerate, channels);
		}
		catch (Exception e) {
			throw new IOException("Deserialize audio format failed.", e);
		}

		return format;
	}

}
