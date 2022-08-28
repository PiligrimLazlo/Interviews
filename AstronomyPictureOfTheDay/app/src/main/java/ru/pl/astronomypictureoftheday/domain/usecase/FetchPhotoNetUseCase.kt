package ru.pl.astronomypictureoftheday.domain.usecase

import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.domain.repository.NetPhotoRepository

class FetchPhotoNetUseCase(private val netPhotoRepository: NetPhotoRepository) {

    suspend operator fun invoke(): PhotoEntity {
        return netPhotoRepository.fetchPhoto()
    }

}