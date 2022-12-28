fun <T> Collection<T>.only(): T = this.also { check(it.size == 1) }.first()
