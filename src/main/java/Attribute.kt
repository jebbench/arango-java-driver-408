import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.reflect.KClass


enum class AttributeType {
    STRING {
        override val string = "string"
        override val clazz = StringAttribute::class
        override fun asAttribute(key: String, value: Any): StringAttribute {
            try {
                return StringAttribute(key, value as String)
            } catch (e: ClassCastException) {
                throw IncorrectAttributeTypeException(key, string)
            }
        }

    },
    NUMBER {
        override val string = "number"
        override val clazz = NumberAttribute::class
            override fun asAttribute(key: String, value: Any): NumberAttribute {
            try {
                return NumberAttribute(key, value as BigDecimal)
            } catch (e: ClassCastException) {
                throw IncorrectAttributeTypeException(key, string)
            }
        }
    },
    BOOLEAN {
        override val string = "bool"
        override val clazz = BooleanAttribute::class
            override fun asAttribute(key: String, value: Any): BooleanAttribute {
            try {
                return BooleanAttribute(key, value as Boolean)
            } catch (e: ClassCastException) {
                throw IncorrectAttributeTypeException(key, string)
            }
        }
    };

    abstract fun asAttribute(key: String, value: Any): Attribute
    abstract val string: String
    abstract val clazz: KClass<out Attribute>

}

class UnknownAttributeTypeException(type: String) : Exception("Unknown attribute type '${type}'.")
class IncorrectAttributeTypeException(key: String, type: String) : Exception("Expected the attribute '${key}' to be of type '${type}'.")


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(
        name = "STRING",
        value = StringAttribute::class
    ),
    JsonSubTypes.Type(
        name = "NUMBER",
        value = NumberAttribute::class
    ),
    JsonSubTypes.Type(
        name = "BOOLEAN",
        value = BooleanAttribute::class
    )
)
sealed class Attribute (open val key: String, open val value: Any){
    abstract val type: AttributeType
    abstract fun asString(locale: Locale? = null): String
}

data class StringAttribute(override val key: String, override val value: String) : Attribute(key, value) {
    override val type = AttributeType.STRING
    override fun asString(locale: Locale?): String {
        return value
    }

}
data class NumberAttribute(override val key: String, override val value: BigDecimal) : Attribute(key, value) {

    override val type = AttributeType.NUMBER
    override fun asString(locale: Locale?): String {
        return value.toString()
    }

}

data class BooleanAttribute(override val key: String, override val value: Boolean) : Attribute(key, value) {
    override val type = AttributeType.BOOLEAN
    override fun asString(locale: Locale?): String {
        return value.toString()
    }
}