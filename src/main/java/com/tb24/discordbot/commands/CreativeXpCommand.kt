package com.tb24.discordbot.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.tb24.discordbot.Rune
import com.tb24.discordbot.util.Utils
import com.tb24.discordbot.util.await
import com.tb24.discordbot.util.dispatchClientCommandRequest
import com.tb24.discordbot.util.getEmoteByName
import com.tb24.fn.model.mcpprofile.commands.QueryProfile

class CreativeXpCommand : BrigadierCommand("creativexp", "Shows info about your daily creative XP.", arrayOf("doihavecreativexp")) {
	override fun getNode(dispatcher: CommandDispatcher<CommandSourceStack>): LiteralArgumentBuilder<CommandSourceStack> = newRootNode()
		.executes {
			val source = it.source
			source.ensureSession()
			source.loading("Getting BR data")
			source.api.profileManager.dispatchClientCommandRequest(QueryProfile(), "athena").await()
			val athena = source.api.profileManager.getProfileData("athena")
			val lastCreativePlaytimeTracker = athena.items.values.firstOrNull { it.templateId == "Quest:quest_br_creative_playtimetracker_4" }
				?: throw SimpleCommandExceptionType(LiteralMessage("No playtime tracker")).create()
			val (current, max) = getQuestCompletion(lastCreativePlaytimeTracker, false)
			val delta = 15
			val xpCount = 6300
			source.complete(if (Rune.isBotDev(source)) getEmoteByName(if (current < max) "yus" else "nu")?.asMention else null, source.createEmbed()
				.setTitle("Creative XP")
				.setDescription("`%s`\n%,d / %,d minutes played\n%,d / %,d %s".format(
					Utils.progress(current, max, 32),
					current, max,
					current / delta * xpCount, max / delta * xpCount, getEmoteByName("AthenaSeasonalXP")?.asMention))
				.build())
			Command.SINGLE_SUCCESS
		}
}