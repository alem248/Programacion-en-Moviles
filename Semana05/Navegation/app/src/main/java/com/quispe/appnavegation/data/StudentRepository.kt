package com.quispe.appnavegation.data

data class Student(
    val id: Int,
    val name: String,
    val career: String,
    val cycle: String,
    val email: String,
    val phone: String,
    val bio: String,
    val initials: String
)

object StudentRepository {
    val students = listOf(
        Student(
            id = 1,
            name = "Juan León Suiyon",
            career = "Diseño y Desarrollo de Software",
            cycle = "V Ciclo",
            email = "juan.leon@tecsup.edu.pe",
            phone = "+51 987 654 321",
            bio = "Estudiante destacado de V ciclo con alto rendimiento académico. Especializado en desarrollo móvil Android nativo con Kotlin y Jetpack Compose. Líder de proyectos de innovación tecnológica en Tecsup.",
            initials = "JL"
        ),
        Student(
            id = 2,
            name = "María García Rojas",
            career = "Ingeniería de Software",
            cycle = "VI Ciclo",
            email = "m.garcia@tecsup.edu.pe",
            phone = "+51 912 345 678",
            bio = "Apasionada por la arquitectura de software, bases de datos no relacionales y desarrollo de API REST con Spring Boot. Participante activa en hackatones de desarrollo web y móvil.",
            initials = "MG"
        ),
        Student(
            id = 3,
            name = "Carlos Mendoza Vera",
            career = "Redes y Comunicaciones",
            cycle = "IV Ciclo",
            email = "c.mendoza@tecsup.edu.pe",
            phone = "+51 955 443 322",
            bio = "Enfocado en ciberseguridad, administración de servidores Linux e infraestructura cloud en AWS. Certificado en Cisco CCNA con distinción.",
            initials = "CM"
        ),
        Student(
            id = 4,
            name = "Ana Flores Quispe",
            career = "Diseño y Desarrollo de Software",
            cycle = "V Ciclo",
            email = "a.flores@tecsup.edu.pe",
            phone = "+51 922 110 099",
            bio = "Diseñadora UX/UI y desarrolladora frontend. Creación de prototipos interactivos en Figma y desarrollo de componentes accesibles e intuitivos con Material Design 3.",
            initials = "AF"
        ),
        Student(
            id = 5,
            name = "Luis Paredes Torres",
            career = "Big Data y Analítica de Datos",
            cycle = "III Ciclo",
            email = "l.paredes@tecsup.edu.pe",
            phone = "+51 944 332 211",
            bio = "Entusiasta de Machine Learning, desarrollo de modelos predictivos con Python y visualización de dashboards interactivos para la toma de decisiones empresariales.",
            initials = "LP"
        ),
        Student(
            id = 6,
            name = "Sofia Ramos Castro",
            career = "Ingeniería de Software",
            cycle = "VI Ciclo",
            email = "s.ramos@tecsup.edu.pe",
            phone = "+51 966 778 899",
            bio = "Desarrolladora Full Stack con experiencia comprobada en Flutter, React y Node.js. Delegada del club de programación avanzada de Tecsup.",
            initials = "SR"
        )
    )

    fun getStudentById(id: Int): Student {
        return students.find { it.id == id } ?: students.first()
    }
}
