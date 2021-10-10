import com.fasterxml.jackson.annotation.*

@JsonIgnoreProperties("_id", "_rev")
data class Element (
    @JsonProperty("_key") val id: String,
    val jobId: String,
    val uniqueIdentifier: String,
    @JsonSetter(contentNulls = Nulls.SKIP) val attributes: Map<String, Attribute> = emptyMap(),
) { }

