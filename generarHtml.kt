import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.io.File

@Serializable
data class Recipe(
    val id: Int,
    val name: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servings: Int,
    val difficulty: String,
    val cuisine: String,
    val caloriesPerServing: Int,
    val tags: List<String>,
    val userId: Int,
    val image: String,
    val rating: Double,
    val reviewCount: Int,
    val mealType: List<String>
)

@Serializable
data class RecipesResponse(
    val recipes: List<Recipe>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

fun deJsonAListaRecetas(): List<Recipe> {
    val client = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://dummyjson.com/recipes"))
        .GET()
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    val jsonBody = response.body()
    val recipesResponse: RecipesResponse = Json.decodeFromString(jsonBody)

    return recipesResponse.recipes
}

fun generarHtml(recipes: List<Recipe>): String {
    return buildString {
        appendHTML().html {
            head {
                title("Lista de recetas")
                style {
                    +"table { width: 100%; border-collapse: collapse; }"
                    +"th, td { border: 1px solid blue; padding: 8px; text-align: left; }"
                    +"th { background-color: #f2f2f2; }"
                }
            }
            body {
                h1 { +"Lista de recetas" }
                table {
                    tr {
                        th { +"Nombre" }
                        th { +"Tipo de comida" }
                        th { +"Tiempo necesario" }
                        th { +"Dificultad" }
                        th { +"Calorías" }
                        th { +"Valoración" }
                    }
                    recipes.sortedByDescending { it.rating }.forEach { recipe ->
                        tr {
                            td { +"${recipe.name}" }
                            td { +"${recipe.mealType}" }
                            td { +"${recipe.prepTimeMinutes + recipe.cookTimeMinutes}" }
                            td { +"${recipe.difficulty}" }
                            td { +"${recipe.caloriesPerServing}" }
                            td { +"${recipe.rating}" }
                        }
                    }
                }
            }
        }
    }
}

fun main() {
    val recipes = deJsonAListaRecetas()
    val htmlContent = generarHtml(recipes)
    File("recetas.html").writeText(htmlContent)
    println("Generado archivo HTML: recetas.html")
}
