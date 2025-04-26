package com.amor.chatclient.service.vectorstore.inmemory;


import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.converter.AbstractFilterExpressionConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class MongoDBVectorStoreFilterExpressionConverter extends AbstractFilterExpressionConverter {

	private static final Pattern DATE_FORMAT_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z");

	private final SimpleDateFormat dateFormat;

	public MongoDBVectorStoreFilterExpressionConverter() {
		this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override
	protected void doExpression(Filter.Expression expression, StringBuilder context) {
		this.convertOperand(expression.left(), context);
		context.append(getOperationSymbol(expression));
		this.convertOperand(expression.right(), context);
	}

	private String getOperationSymbol(Filter.Expression exp) {
		return switch (exp.type()) {
			case AND -> " and ";
			case OR -> " or ";
			case EQ -> " == ";
			case LT -> " < ";
			case LTE -> " <= ";
			case GT -> " > ";
			case GTE -> " >= ";
			case NE -> " != ";
			case IN -> " in ";
			case NIN -> " not in ";
			default -> throw new RuntimeException("Not supported expression type: " + exp.type());
		};
	}

	@Override
	protected void doKey(Filter.Key key, StringBuilder context) {
		var identifier = hasOuterQuotes(key.key()) ? removeOuterQuotes(key.key()) : key.key();
		context.append("#metadata['").append(identifier).append("']");
	}

	@Override
	protected void doValue(Filter.Value filterValue, StringBuilder context) {
		if (filterValue.value() instanceof List<?> list) {
			var formattedList = new StringBuilder("{");
			int c = 0;
			for (Object v : list) {
				this.doSingleValue(v, formattedList);
				if (c++ < list.size() - 1) {
					this.doAddValueRangeSpitter(filterValue, formattedList);
				}
			}
			formattedList.append("}");

			if (context.lastIndexOf("in ") == -1) {
				context.append(formattedList);
			}
			else {
				appendSpELContains(formattedList, context);
			}
		}
		else {
			this.doSingleValue(filterValue.value(), context);
		}
	}

	private void appendSpELContains(StringBuilder formattedList, StringBuilder context) {
		int metadataStart = context.lastIndexOf("#metadata");
		if (metadataStart == -1)
			throw new RuntimeException("Wrong SpEL expression: " + context);

		int metadataEnd = context.indexOf(" ", metadataStart);
		String metadata = context.substring(metadataStart, metadataEnd);
		context.setLength(context.lastIndexOf("in "));
		context.delete(metadataStart, metadataEnd + 1);
		context.append(formattedList).append(".contains(").append(metadata).append(")");
	}

	@Override
	protected void doSingleValue(Object value, StringBuilder context) {
		if (value instanceof Date date) {
			context.append("'");
			context.append(this.dateFormat.format(date));
			context.append("'");
		}
		else if (value instanceof String text) {
			context.append("'");
			if (DATE_FORMAT_PATTERN.matcher(text).matches()) {
				try {
					Date date = this.dateFormat.parse(text);
					context.append(this.dateFormat.format(date));
				}
				catch (ParseException e) {
					throw new IllegalArgumentException("Invalid date type:" + text, e);
				}
			}
			else {
				context.append(text);
			}
			context.append("'");
		}
		else {
			context.append(value);
		}
	}

	@Override
	protected void doGroup(Filter.Group group, StringBuilder context) {
		context.append("(");
		super.doGroup(group, context);
		context.append(")");
	}

}
